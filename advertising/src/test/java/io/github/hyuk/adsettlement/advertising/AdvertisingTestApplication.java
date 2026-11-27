package io.github.hyuk.adsettlement.advertising;

import io.github.hyuk.adsettlement.advertising.presentation.AdvertiserController;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@ComponentScan(basePackageClasses = AdvertiserController.class)
public class AdvertisingTestApplication {
}
