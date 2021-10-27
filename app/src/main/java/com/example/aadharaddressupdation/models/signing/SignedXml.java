package com.example.aadharaddressupdation.models.signing;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Base64;

/**
 * Represents a signed XML
 */
public class SignedXml {

    private String content;

    public SignedXml(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String toBase64() {
        String base64Xml = Base64.getEncoder().encodeToString(content.getBytes());
        return base64Xml;
    }
}