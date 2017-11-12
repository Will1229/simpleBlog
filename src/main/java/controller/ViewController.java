package controller;

import config.ServiceConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Import(ServiceConfiguration.class)
@RequestMapping(path = "/view")
public class ViewController {
}
