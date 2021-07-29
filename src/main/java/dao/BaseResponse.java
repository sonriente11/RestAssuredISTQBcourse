package dao;

public class BaseResponse {
    private Integer code;
    private MetaInformation meta;
    private User data;

    public BaseResponse() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public MetaInformation getMeta() {
        return meta;
    }

    public void setMeta(MetaInformation meta) {
        this.meta = meta;
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}
