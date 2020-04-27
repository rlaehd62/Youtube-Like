package kid.youtube.Entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Category
{
    @Id private String name;
    public Category() {}
    public Category(String name)
    {
        setName(name);
    }
}
