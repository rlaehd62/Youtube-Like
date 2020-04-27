package kid.youtube.Repository;

import kid.youtube.Entity.Category;
import kid.youtube.Entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface VideoRepository extends JpaRepository<Video, String>
{
    @Transactional void deleteVideoByUuid(String uuid);
    List<Video> findAllByCategory_Name(String name);
}
