package com.example.notification.model.enums;

public enum Topic {
    RECEIVED_ORDER("/receivedOrder"),
    UPDATE_ORDER("/updateOrder"),
    RECEIVED_REVIEW("/receivedReview");

    private final String destinationPath;

    Topic(String destinationPath) {
        this.destinationPath = destinationPath;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public static Topic fromPath(String path) {
        for (Topic topic : values()) {
            if (topic.getDestinationPath().equals(path) || topic.getDestinationPath().equals("/" + path)) {
                return topic;
            }
        }
        throw new IllegalArgumentException("Topic necunoscut pentru calea: " + path);
    }

}
