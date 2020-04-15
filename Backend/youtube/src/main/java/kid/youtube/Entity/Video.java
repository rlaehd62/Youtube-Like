package kid.youtube.Entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Video
{
    @Id private String uuid;

    @Enumerated(EnumType.STRING) private Category category;

    private String title;
    private String uploader;

    @Temporal(TemporalType.DATE)
    private Date date;

    public Video() {}
    public Video(String uuid, Category category, String title, String uploader)
    {
        setUuid(uuid);
        setTitle(title);
        setCategory(category);
        setUploader(uploader);
        setDate(new Date());
    }
}
