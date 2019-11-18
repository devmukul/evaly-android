package bd.com.evaly.evalyshop.models.issue;

import java.util.List;

public class IssuesModel {
    private int id;
    private String issue_type;
    private String attachment;
    private String created_at;
    private String updated_at;
    private String description;
    private List<ReplyModel> issue_replies;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIssue_type() {
        return issue_type;
    }

    public void setIssue_type(String issue_type) {
        this.issue_type = issue_type;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ReplyModel> getIssue_replies() {
        return issue_replies;
    }

    public void setIssue_replies(List<ReplyModel> issue_replies) {
        this.issue_replies = issue_replies;
    }

    public class ReplyModel{
        private int id;
        private String body;
        private String attachement;
        private String updated_at;
        private String created_at;
        private RepliedByModel reply_by;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getAttachement() {
            return attachement;
        }

        public void setAttachement(String attachement) {
            this.attachement = attachement;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public RepliedByModel getReply_by() {
            return reply_by;
        }

        public void setReply_by(RepliedByModel reply_by) {
            this.reply_by = reply_by;
        }
    }

    public class RepliedByModel{
        private String id;
        private String username;
        private String first_name;
        private String last_name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public void setLast_name(String last_name) {
            this.last_name = last_name;
        }
    }

}
