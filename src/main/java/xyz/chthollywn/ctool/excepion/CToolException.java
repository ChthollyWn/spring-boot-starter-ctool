package xyz.chthollywn.ctool.excepion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CToolException extends RuntimeException{
    private final String message;
}
