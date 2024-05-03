package ori.model;

import org.hibernate.validator.constraints.Length;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotEmpty; //spring boot 3.1.5

import lombok.*;

@Data

@AllArgsConstructor

@NoArgsConstructor

public class BrandModel {

private Integer brandId;

//validate

@NotEmpty

@Length(min=5)

private String name;

private String logo;

private MultipartFile imageFile;

private Boolean isEdit=false;

}