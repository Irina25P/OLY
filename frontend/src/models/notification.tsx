export interface NotificationDto {
    id: string;
    receiverId: string;
    message: string;
}

export interface NotificationAckDto {
    notificationId: string;
    receiverId: string;
}