package com.wealoha.social.inject;

import dagger.Module;

/**
 * 所有的模块(Android和自定义的)
 * 
 * @author javamonk
 * @see
 * @since
 * @date 2014-10-28 下午3:25:50
 */
@Module(includes = { AndroidModule.class, AlohaModule.class })
public class RootModule {
}
