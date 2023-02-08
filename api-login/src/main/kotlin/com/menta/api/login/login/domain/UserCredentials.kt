import com.menta.api.login.shared.domain.UserType

data class UserCredentials(
    val user: String,
    val password: String,
    val userType: UserType
) {

    override fun toString(): String {
        return "UserCredentials(user='$user', userType=$userType)"
    }
}
