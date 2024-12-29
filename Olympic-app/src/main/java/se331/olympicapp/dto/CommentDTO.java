package se331.olympicapp.dto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentDTO {
    private Long id;
    private String nickname;
    private String content;
    private String avatarUrl;
    private Long userId;
    private String countryCode;

    public CommentDTO(Long id, String nickname, String content, String avatarUrl, Long userId, String countryCode) {
        this.id = id;
        this.nickname = nickname;
        this.content = content;
        this.avatarUrl = avatarUrl;
        this.userId = userId;
        this.countryCode = countryCode;
    }

}
