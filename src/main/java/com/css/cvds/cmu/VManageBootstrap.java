package com.css.cvds.cmu;

import java.util.logging.LogManager;

import com.css.cvds.cmu.conf.druid.EnableDruidSupport;
import com.css.cvds.cmu.utils.GitUtil;
import com.css.cvds.cmu.utils.SpringBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 * @author chend
 */
@ServletComponentScan("com.css.cvds.cmu.conf")
@SpringBootApplication
@EnableScheduling
@EnableDruidSupport
public class VManageBootstrap extends LogManager {

	private final static Logger logger = LoggerFactory.getLogger(VManageBootstrap.class);

	private static String[] args;
	private static ConfigurableApplicationContext context;
	public static void main(String[] args) {
		VManageBootstrap.args = args;
		VManageBootstrap.context = SpringApplication.run(VManageBootstrap.class, args);
		GitUtil gitUtil1 = SpringBeanFactory.getBean("gitUtil");
		logger.info("构建版本： {}", gitUtil1.getBuildVersion());
		logger.info("构建时间： {}", gitUtil1.getBuildDate());
		logger.info("GIT最后提交时间： {}", gitUtil1.getCommitTime());
	}
	/**
	 * 项目重启
	 */
	public static void restart() {
		context.close();
		VManageBootstrap.context = SpringApplication.run(VManageBootstrap.class, args);
	}
}
