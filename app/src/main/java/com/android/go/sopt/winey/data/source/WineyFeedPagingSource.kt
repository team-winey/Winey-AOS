import com.android.go.sopt.winey.data.service.AuthService
import com.android.go.sopt.winey.data.source.BasePagingSource
import com.android.go.sopt.winey.domain.entity.WineyFeed
import javax.inject.Inject

class WineyFeedPagingSource @Inject constructor(
    authService: AuthService
) : BasePagingSource(authService) {
    override suspend fun getFeedList(position: Int): List<WineyFeed> {
        return authService.getWineyFeedList(position).toWineyFeed()
    }
}
