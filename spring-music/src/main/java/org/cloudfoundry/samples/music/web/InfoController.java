package org.cloudfoundry.samples.music.web;

import org.cloudfoundry.samples.music.domain.ApplicationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    private Environment springEnvironment;

    @Autowired
    public InfoController(Environment springEnvironment) {
        this.springEnvironment = springEnvironment;
    }

    @RequestMapping(value = "/appinfo")
    public ApplicationInfo info() {
        return new ApplicationInfo(springEnvironment.getActiveProfiles(), new String[]{});
    }
}
