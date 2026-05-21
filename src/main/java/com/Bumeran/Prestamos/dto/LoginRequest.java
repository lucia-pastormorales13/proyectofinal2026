package com.Bumeran.Prestamos.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;

}

//hacemos un dto del login y el registro para no exponer la entidad Usuario directamente al frontend, y para tener una estructura clara de los datos que se esperan en cada caso.