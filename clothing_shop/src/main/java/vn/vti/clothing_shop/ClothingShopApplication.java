package vn.vti.clothing_shop;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClothingShopApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		System.setProperty("POSTGRESQL_URL", "jdbc:postgresql://"+System.getProperty("POSTGRESQL_EXTERNAL_HOST")+":"+System.getProperty("POSTGRESQL_PORT")+"/"+System.getProperty("POSTGRESQL_DATABASE")+"?user="+System.getProperty("POSTGRESQL_USERNAME")+"&password="+System.getProperty("POSTGRESQL_PASSWORD"));
		SpringApplication.run(ClothingShopApplication.class, args);
	}

}
