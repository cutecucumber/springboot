package com.lz.lettuce.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * @author lizhao
 * @class User
 * @description
 * @create 2023/11/23 11:28
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    String id;
    String username;
}
