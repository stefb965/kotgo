package cn.nekocode.kotgo.sample.data.DO

import android.os.Parcel
import android.os.Parcelable
import cn.nekocode.kotgo.sample.data.base.DO
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
@PaperParcel
class Meizi(
        @SerializedName("_id") val id: String,
        val type: String,
        val url: String,
        val who: String
) : DO, Parcelable {

    companion object {
        @JvmField val CREATOR = PaperParcelMeizi.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelMeizi.writeToParcel(this, dest, flags)
    }
}