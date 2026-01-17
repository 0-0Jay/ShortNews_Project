import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.example.shortnews.UserSharedPreferences
import com.example.shortnews.ui.member.MyPageFragment
import java.io.File

class S3Service {
    private var accessKey = "" // IAM AccessKey
    private var secretKey = "" // IAM SecretKey
    private var region // S3 Region
            : Region

    /**
     * 생성자 생성 시 초기 Region 설정 : AP_NORTHEAST_2
     */
    init {
        region = Region.getRegion(Regions.AP_NORTHEAST_2)
    }

    /**
     * Overloading
     */
    fun uploadWithTransferUtility(
        context: Context?,
        bucketName: String,
        file: File,
        fileName: String?,
        listener: TransferListener?
    ) {
        this.uploadWithTransferUtility(
            context,
            bucketName, null, file, fileName,
            listener
        )
    }

    /**
     * S3 파일 업로드
     *
     * @param context    Context
     * @param bucketName S3 버킷 이름(/(슬래쉬) 없이)
     * @param folder     버킷 내 폴더 경로(/(슬래쉬) 맨 앞, 맨 뒤 없이)
     * @param fileName   파일 이름
     * @param file       Local 파일 경로
     * @param listener   AWS S3 TransferListener
     */
    fun uploadWithTransferUtility(
        context: Context?,
        bucketName: String,
        folder: String?,
        file: File,
        fileName: String?,
        listener: TransferListener?
    ) {
        require(!(TextUtils.isEmpty(accessKey) || TextUtils.isEmpty(secretKey))) { "AccessKey & SecretKey must be not null" }
        val awsCredentials: AWSCredentials = BasicAWSCredentials(
            accessKey, secretKey
        )
        val s3Client = AmazonS3Client(
            awsCredentials, region
        )
        val transferUtility = TransferUtility.builder()
            .s3Client(s3Client)
            .context(context)
            .build()
        TransferNetworkLossHandler.getInstance(context)
        val uploadObserver = transferUtility.upload(
            if (TextUtils.isEmpty(folder)) bucketName else "$bucketName/$folder",
            if (TextUtils.isEmpty(fileName)) file.name else fileName,
            file
        )
        uploadObserver.setTransferListener(listener)
    }

    /**
     * Access, Secret Key 설정
     */
    fun setKeys(accessKey: String, secretKey: String): S3Service {
        this.accessKey = accessKey
        this.secretKey = secretKey
        return this
    }

    /**
     * Access Key 설정
     */
    fun setAccessKey(accessKey: String): S3Service {
        this.accessKey = accessKey
        return this
    }

    /**
     * Secret Key 설정
     */
    fun setSecretKey(secretKey: String): S3Service {
        this.secretKey = secretKey
        return this
    }

    /**
     * Region Enum 으로 Region 설정
     */
    fun setRegion(regionName: Regions?): S3Service {
        region = Region.getRegion(regionName)
        return this
    }

    /**
     * Region Class 로 Region 설정
     */
    fun setRegion(region: Region): S3Service {
        this.region = region
        return this
    }

    object LHolder {
        val instance = S3Service()
    }

    companion object {
        val instance: S3Service
            /**
             * Singleton Pattern
             */
            get() = LHolder.instance
    }

    fun deleteObjectFromS3(bucketName: String, id: String) {
        val awsCredentials: AWSCredentials = BasicAWSCredentials(
            accessKey, secretKey
        )
        val s3Client = AmazonS3Client(
            awsCredentials, region
        )

        val bucketName = bucketName
        val id = id

        try {
            val deleteObjectRequest = DeleteObjectRequest(bucketName, id)
            s3Client.deleteObject(deleteObjectRequest)
            Log.d("이미지 삭제 응답", "이미지 삭제 성공")

            val editor = UserSharedPreferences.sharedPreferences.edit()
            editor.putString("profileImage", "https://snewsuserprofile.s3.ap-northeast-2.amazonaws.com/${id}")
            editor.apply()

        } catch (e: Exception) {
            // 예외 처리
            Log.d("이미지 삭제 실패 응답", e.toString())
        }
    }

}

