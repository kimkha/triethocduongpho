package com.kimkha.triethocduongpho.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * @author kimkha
 * @version 1.3
 * @since 5/20/15
 */
@Entity
public class PrivateInfo {
    @Id
    private Long id;

    private String fingerprint;
    private String packageName;

    public PrivateInfo() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
