package pokeraidbot.domain.errors;

import net.dv8tion.jda.core.entities.User;

public class UserMessedUpException extends RuntimeException {
    public UserMessedUpException(User user, String message) {
        super(user.getAsMention() + ": " + message);
    }

    public UserMessedUpException(String userName, String message) {
        super(userName + ": " + message);
    }
}
