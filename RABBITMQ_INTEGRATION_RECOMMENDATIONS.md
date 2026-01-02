# Gợi ý Tích hợp RabbitMQ cho Reading Novel Platform

## 📊 Tổng quan

Sau khi quét qua project, đây là các điểm **ưu tiên cao** nên tích hợp RabbitMQ để tối ưu performance và scalability:

---

## 🎯 1. NOTIFICATION SYSTEM (Ưu tiên CAO)

### Vấn đề hiện tại:
- **File**: `ChapterServiceImpl.java` (dòng 74-94)
- Khi tạo chapter mới, hệ thống loop qua **TẤT CẢ followers** và gửi notification **đồng bộ**
- Nếu story có 1000+ followers → request sẽ bị block rất lâu
- Notification được gửi ngay trong transaction → có thể rollback nếu lỗi

### Giải pháp với RabbitMQ:
```java
// Queue: notification.new-chapter
// Message: {
//   "storyId": 123,
//   "storyTitle": "...",
//   "chapterNumber": 5,
//   "chapterTitle": "...",
//   "translatorId": 456
// }
```

**Lợi ích:**
- ✅ Response time giảm từ vài giây xuống < 100ms
- ✅ Có thể scale worker riêng để xử lý notification
- ✅ Retry tự động nếu lỗi
- ✅ Không block main transaction

**Files cần sửa:**
- `ChapterServiceImpl.createChapter()` - Gửi message vào queue thay vì gọi trực tiếp
- Tạo `NotificationConsumer` để xử lý message async

---

## 📤 2. FILE UPLOAD TO GITHUB (Ưu tiên CAO)

### Vấn đề hiện tại:
- **File**: `UploadController.java`, `GitHubServiceImpl.java`
- Upload file lên GitHub là **blocking operation** (có thể mất 2-5 giây)
- User phải đợi response trước khi có thể tiếp tục

### Giải pháp với RabbitMQ:
```java
// Queue: upload.image
// Message: {
//   "fileData": base64,
//   "fileName": "...",
//   "folder": "cover",
//   "userId": 123,
//   "requestId": "uuid"
// }
```

**Lợi ích:**
- ✅ User nhận response ngay (trả về requestId)
- ✅ Upload xử lý background
- ✅ Có thể batch upload nhiều file
- ✅ Retry nếu GitHub API fail

**Files cần sửa:**
- `UploadController` - Trả về requestId ngay
- `GitHubServiceImpl` - Move logic vào consumer
- Tạo `UploadConsumer` để xử lý upload async

---

## 💳 3. PAYMENT WEBHOOK PROCESSING (Ưu tiên TRUNG BÌNH)

### Vấn đề hiện tại:
- **File**: `PaymentController.payosTransferHandler()`
- Xử lý webhook payment **đồng bộ** trong controller
- Nếu xử lý lâu → PayOS có thể retry webhook

### Giải pháp với RabbitMQ:
```java
// Queue: payment.webhook
// Message: {
//   "webhookData": {...},
//   "timestamp": "...",
//   "signature": "..."
// }
```

**Lợi ích:**
- ✅ Trả response cho PayOS ngay (< 200ms)
- ✅ Xử lý payment logic async
- ✅ Có thể xử lý nhiều webhook đồng thời
- ✅ Dead letter queue cho failed payments

**Files cần sửa:**
- `PaymentController` - Chỉ verify signature, sau đó gửi vào queue
- Tạo `PaymentWebhookConsumer` để xử lý payment logic

---

## 📊 4. STATISTICS & ANALYTICS (Ưu tiên THẤP)

### Use cases:
- Tính toán view count, follow count
- Update story rankings
- Generate reports
- Cache warming

### Giải pháp với RabbitMQ:
```java
// Queue: analytics.story-view
// Queue: analytics.story-follow
// Queue: analytics.ranking-update
```

**Lợi ích:**
- ✅ Không block user actions
- ✅ Có thể batch process
- ✅ Có thể schedule jobs

---

## 🔄 5. CACHE INVALIDATION (Ưu tiên THẤP)

### Use cases:
- Khi story/chapter được update → invalidate cache
- Khi user follow/unfollow → update cache

### Giải pháp với RabbitMQ:
```java
// Queue: cache.invalidate
// Message: {
//   "cacheKey": "recentlyUpdatedStories:0_10",
//   "pattern": "recentlyUpdatedStories:*"
// }
```

**Lợi ích:**
- ✅ Async cache invalidation
- ✅ Có thể broadcast đến nhiều instances
- ✅ Không block main operations

---

## 🏗️ Kiến trúc đề xuất

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       ▼
┌─────────────────┐
│  REST API       │
│  (Spring Boot)  │
└──────┬──────────┘
       │
       ├──► RabbitMQ ──► Notification Worker
       ├──► RabbitMQ ──► Upload Worker  
       ├──► RabbitMQ ──► Payment Worker
       └──► Database
```

---

## 📝 Implementation Plan

### Phase 1: Notification System (Week 1)
1. Setup RabbitMQ configuration
2. Tạo `NotificationProducer` service
3. Tạo `NotificationConsumer` worker
4. Update `ChapterServiceImpl` để gửi message
5. Test với 1000+ followers

### Phase 2: File Upload (Week 2)
1. Tạo `UploadProducer` service
2. Tạo `UploadConsumer` worker
3. Update `UploadController` để async
4. Implement polling endpoint để check status

### Phase 3: Payment Webhook (Week 3)
1. Tạo `PaymentWebhookProducer` service
2. Tạo `PaymentWebhookConsumer` worker
3. Update `PaymentController` để async
4. Implement retry mechanism

---

## 🔧 Dependencies cần thêm

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

---

## 📈 Expected Performance Improvements

| Feature | Before | After | Improvement |
|---------|--------|-------|-------------|
| Create Chapter (1000 followers) | ~5-10s | ~100ms | **50-100x faster** |
| Upload Image | ~2-5s | ~50ms | **40-100x faster** |
| Payment Webhook | ~500ms | ~50ms | **10x faster** |

---

## ⚠️ Lưu ý quan trọng

1. **Message Durability**: Đảm bảo messages được persist để không mất khi server restart
2. **Dead Letter Queue**: Setup DLQ cho failed messages
3. **Monitoring**: Monitor queue length, consumer lag
4. **Error Handling**: Implement retry với exponential backoff
5. **Idempotency**: Đảm bảo consumers xử lý message idempotent

---

## 🎯 Kết luận

**Ưu tiên triển khai:**
1. ✅ **Notification System** - Impact cao nhất, dễ implement
2. ✅ **File Upload** - Cải thiện UX đáng kể
3. ✅ **Payment Webhook** - Tăng reliability

Các điểm khác (Statistics, Cache) có thể làm sau khi đã có infrastructure RabbitMQ sẵn.

