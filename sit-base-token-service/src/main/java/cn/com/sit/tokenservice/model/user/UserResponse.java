package cn.com.sit.tokenservice.model.user;


import cn.com.sit.tokenservice.model.response.OperationResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UserResponse extends OperationResponse {
    private User data = new User();
}
