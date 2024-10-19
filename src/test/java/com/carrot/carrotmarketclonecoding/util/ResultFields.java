package com.carrot.carrotmarketclonecoding.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.test.web.servlet.ResultMatcher;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResultFields {
    private ResultMatcher resultMatcher;
    private int status;
    private boolean result;
    private String message;
}
