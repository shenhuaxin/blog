package springboot.jpa.entity;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class User {

    @Id
    private String id;

    private String name;

    private String email;

    private Date createTime;
}
