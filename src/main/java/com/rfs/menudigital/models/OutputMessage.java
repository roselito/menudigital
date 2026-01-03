package com.rfs.menudigital.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author Roselito@RFS
 */
@Data
@AllArgsConstructor
public class OutputMessage {
    private String from;
    private String text;
    private String time;
}
