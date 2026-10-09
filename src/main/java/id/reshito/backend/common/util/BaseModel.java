package id.reshito.backend.common.util;

import jakarta.annotation.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;

import java.util.Date;

public class BaseModel {
    @CreatedDate
    protected Date createdAt;

    @LastModifiedBy
    protected Date updatedAt;

    @Nullable
    protected Date deletedAt;
}
