package kr.or.hanium.shareseoul;

import android.app.Activity;

import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.LocationTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

/**
 * Created by jun097kim on 17. 11. 26.
 */

public class Share {
    public Share(Activity activity, String name, String address) {
        LocationTemplate params = LocationTemplate.newBuilder(address,
                ContentObject.newBuilder(name,
                        "http://www.kakaocorp.com/images/logo/og_daumkakao_151001.png",
                        LinkObject.newBuilder()
                                .setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com")
                                .build())
                        .setDescrption(address)
                        .build())
                .setAddressTitle(name)
                .build();

        KakaoLinkService.getInstance().sendDefault(activity, params, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {

            }
        });
    }
}
