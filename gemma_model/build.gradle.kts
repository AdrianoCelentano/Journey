plugins {
    id("com.android.asset-pack")
}

assetPack {
    packName.set("gemma_model")
    dynamicDelivery { // or simply deliveryType.set("fast-follow") if using pure AGP format, let's use the standard
        deliveryType.set("fast-follow")
    }
}
