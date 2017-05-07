package buyerseller.cs646.sdsu.edu.sellit;

import java.util.Date;

/**
 * Created by AnuragG on 06-May-17.
 */

public class ChatModel {
    public String sender;
    public String receiver;
    public String senderUid;
    public String receiverUid;
    public String message;
    public Date timestamp;

    public ChatModel(){}

    public ChatModel(String sender, String receiver, String senderUid, String receiverUid, String message, Date timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
