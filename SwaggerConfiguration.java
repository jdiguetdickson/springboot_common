package com.thermador.magento.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@PropertySource("classpath:application.properties")
public class SwaggerConfiguration extends WebMvcConfigurerAdapter {
    @Value("${swagger.api.title}")
    private String title;

    @Value("${swagger.api.description}")
    private String description;
    @Value("${build.version}")
    private String buildVersion;

    @Bean
    public Docket postMatchApi() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage("com.thermador.magento"))
                .paths(PathSelectors.ant("/**")).build().apiInfo(this.metaData());
    }

    private ApiInfo metaData() {

       /* String version = "1";
        try {
            final MavenXpp3Reader reader = new MavenXpp3Reader();
            final Model model = reader.read(new FileReader("pom.xml"));
            version = model.getVersion();
        } catch (final IOException | XmlPullParserException e) {
        }*/
        return new ApiInfoBuilder().version(this.buildVersion).title(this.title).description(this.description).build();
    }
}
