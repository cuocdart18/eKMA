package com.app.ekma.common.super_utils.view.view_pager_transformer

import android.os.Parcelable
import androidx.viewpager2.widget.ViewPager2
import com.app.ekma.common.super_utils.view.view_pager_transformer.transformers.BackgroundToForegroundPageTransformer
import com.app.ekma.common.super_utils.view.view_pager_transformer.transformers.CubeInPageTransformer
import com.app.ekma.common.super_utils.view.view_pager_transformer.transformers.CubeOutTransformer
import com.app.ekma.common.super_utils.view.view_pager_transformer.transformers.ForegroundToBackgroundPageTransformer
import com.app.ekma.common.super_utils.view.view_pager_transformer.transformers.RotateDownPageTransformer
import com.app.ekma.common.super_utils.view.view_pager_transformer.transformers.RotateUpPageTransformer
import com.app.ekma.common.super_utils.view.view_pager_transformer.transformers.ZoomInTransformer
import com.app.ekma.common.super_utils.view.view_pager_transformer.transformers.ZoomOutPageTransformer
import kotlinx.parcelize.Parcelize

@Parcelize
enum class PageTransformer : Parcelable {
    BACKGROUND_TO_FOREGROUND_TRANSFORMER,
    CUBE_OUT_TRANSFORMER,
    FOREGROUND_TO_BACKGROUND_TRANSFORMER,
    ZOOM_OUT_PAGE_TRANSFORMER,
    CUBE_IN_TRANSFORMER,
    ROTATE_DOWN_PAGE_TRANSFORMER,
    ROTATE_UP_PAGE_TRANSFORMER,
    ZOOM_IN_TRANSFORMER
}


fun getPageTransformer(pageTransformer: PageTransformer): ViewPager2.PageTransformer {
    return when (pageTransformer) {
        PageTransformer.BACKGROUND_TO_FOREGROUND_TRANSFORMER -> BackgroundToForegroundPageTransformer()
        PageTransformer.FOREGROUND_TO_BACKGROUND_TRANSFORMER -> ForegroundToBackgroundPageTransformer()
        PageTransformer.CUBE_OUT_TRANSFORMER -> CubeOutTransformer()
        PageTransformer.ZOOM_OUT_PAGE_TRANSFORMER -> ZoomOutPageTransformer()

        PageTransformer.CUBE_IN_TRANSFORMER -> CubeInPageTransformer()
        PageTransformer.ROTATE_DOWN_PAGE_TRANSFORMER -> RotateDownPageTransformer()
        PageTransformer.ROTATE_UP_PAGE_TRANSFORMER -> RotateUpPageTransformer()
        PageTransformer.ZOOM_IN_TRANSFORMER -> ZoomInTransformer()
    }
}
