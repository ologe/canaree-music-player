package dev.olog.msc.presentation.invite.friends

import android.net.Uri
import android.os.Build
import com.google.android.gms.appinvite.AppInviteInvitation
import dev.olog.msc.R
import dev.olog.msc.app.app
import dev.olog.msc.presentation.base.BaseBottomSheetFragment
import dev.olog.msc.presentation.main.MainActivity
import dev.olog.msc.utils.k.extension.act
import kotlinx.android.synthetic.main.fragment_invite_friends.*

class InviteFriendsFragment: BaseBottomSheetFragment(){

    companion object {
        const val TAG = "InviteFriendsFragment"
    }

    override fun onResume() {
        super.onResume()
        share_sms.setOnClickListener {
           sendInviteSmsIntent()
            dismiss()
        }

        share_email.setOnClickListener {
            sendInviteEmailIntent()
            dismiss()
        }
    }

    private fun sendInviteSmsIntent(){
        val intent = AppInviteInvitation.IntentBuilder(app.getString(R.string.share_app_title))
                .setMessage(app.getString(R.string.share_app_message))
                .setDeepLink(Uri.parse("https://deveugeniuolog.wixsite.com/next"))
                .setCallToActionText("OK")
                .setAndroidMinimumVersionCode(Build.VERSION_CODES.LOLLIPOP)
                .build()
        act.startActivityForResult(intent, MainActivity.INVITE_FRIEND_CODE)
    }

    private fun sendInviteEmailIntent(){
        val intent = AppInviteInvitation.IntentBuilder(app.getString(R.string.share_app_title))
                .setMessage(app.getString(R.string.share_app_message))
                .setDeepLink(Uri.parse("https://deveugeniuolog.wixsite.com/next"))
                .setEmailSubject("Invite friends")
                .setEmailHtmlContent("test")
                .setAndroidMinimumVersionCode(Build.VERSION_CODES.LOLLIPOP)
                .build()
        act.startActivityForResult(intent, MainActivity.INVITE_FRIEND_CODE)
    }

    override fun onPause() {
        super.onPause()
        share_sms.setOnClickListener(null)
        share_email.setOnClickListener(null)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_invite_friends
}