CREATE TABLE IF NOT EXISTS sunbird.user_device (deviceId text,userId text,language text,fcmToken text,topic list<text>,PRIMARY KEY(deviceId));
