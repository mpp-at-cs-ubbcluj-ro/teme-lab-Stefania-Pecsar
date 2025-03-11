package ro.mpp2024;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CarsDBRepository implements CarRepository{

    private JdbcUtils dbUtils;



    private static final Logger logger= LogManager.getLogger();

    public CarsDBRepository(Properties props) {
        logger.info("Initializing CarsDBRepository with properties: {} ",props);
        dbUtils=new JdbcUtils(props);
    }

    @Override
    public List<Car> findByManufacturer(String manufacturerN) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Car> cars = new ArrayList<>();
        try(PreparedStatement preStmt = con.prepareStatement("select * from cars_dg_tmp where manufacturer = ?")){
            preStmt.setString(1, manufacturerN);
            try(ResultSet result = preStmt.executeQuery()){
                while (result.next()){
                    int id = result.getInt("id");
                    String manufacturer = result.getString("manufacturer");
                    String model = result.getString("model");
                    int year = result.getInt("year");
                    Car car = new Car(manufacturer, model, year);
                    car.setId(id);
                    cars.add(car);
                }
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit();
    return cars;
    }

    @Override
    public List<Car> findBetweenYears(int min, int max) {
        //to do
        return null;
    }

    @Override
    public void add(Car elem) {
        logger.traceEntry("saving task {} ",elem);
        Connection con=dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("insert into cars_dg_tmp(manufacturer,model,year) values (?,?,?)")) {
            preStmt.setString(1, elem.getManufacturer());
            preStmt.setString(2, elem.getModel());
            preStmt.setInt(3, elem.getYear());
            int result = preStmt.executeUpdate();
            logger.info("saved {} instances", result);
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB " + e);
        }
        logger.traceExit();

    }

    @Override
    public void update(Integer integer, Car elem) {
        //to do
    }

    @Override
    public Iterable<Car> findAll() {
       logger.traceEntry();
       Connection con = dbUtils.getConnection();
       List<Car> cars=new ArrayList<>();
       try(PreparedStatement preStmt = con.prepareStatement("select * from cars_dg_tmp")) {
           try(ResultSet result = preStmt.executeQuery()){
               while(result.next()){
                   int id = result.getInt("id");
                   String manufacturer = result.getString("manufacturer");
                   String model = result.getString("model");
                   int year = result.getInt("year");
                   Car car = new Car(manufacturer,model,year);
                   car.setId(id);
                   cars.add(car);
               }
           }
       }catch (SQLException e){
           logger.error(e);
           System.err.println("Error DB " + e);
       }
        logger.traceExit(cars);
       return cars;
    }
}
