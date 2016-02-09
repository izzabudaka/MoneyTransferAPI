package Utility;

import java.sql.SQLException;

/**
 * Created by Home on 09/02/16.
 */
public abstract class RevolutTransferException extends SQLException {
    protected final String message;

    protected RevolutTransferException(String message) {
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
