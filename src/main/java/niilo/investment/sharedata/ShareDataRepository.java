package niilo.investment.sharedata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareDataRepository extends JpaRepository<ShareData,Long> {
}
