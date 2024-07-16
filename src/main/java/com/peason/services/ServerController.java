package com.peason.services;

import com.peason.model.TableRows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.sql.SQLException;

@RestController
@Component
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ServerController {

  @Autowired
  ServersAndTablesRepository serversAndTablesRepository; //= springClasses.getServersAndTablesRepository();
  //@Autowired
  //DAO dao; //=springClasses.getDao();

  @PostConstruct
  public void setup() {
    System.out.println("created ServerController");
  }

  @RequestMapping(value = "/health_check", method = RequestMethod.GET)
  public ResponseEntity<String> healthcheck() {
    return new ResponseEntity<String>("Crypto Trades is running just fine..", HttpStatus.OK);
  }

  @GetMapping(value="/tables/{selectedServer}")
  //@CrossOrigin
  public String  tableList(@PathVariable("selectedServer") String serverName)  {
    System.out.println("table list requested for:" +serverName);
    String tl= serversAndTablesRepository.getTableNames(serverName);
    System.out.println("tl>>:" +tl);
    return tl;
  }

  @GetMapping(value="/servers")
  //@CrossOrigin
  public String serverList()  {
    return serversAndTablesRepository.getServerNames();

  }

  @GetMapping(value="/columns/{selectedServer}/{selectedTable}")
  @CrossOrigin
  public String  columnList(@PathVariable("selectedServer")String serverName, @PathVariable("selectedTable") String tableName)  {
    System.out.println("column list requested for:" +tableName+ " on server "+ serverName);
    String cl= serversAndTablesRepository.getTableColumns(serverName, tableName);
    System.out.println("column list requested:"+cl);
    return cl;

  }

  @GetMapping(value="/columndata/{selectedServer}/{selectedTable}")
  @CrossOrigin
  public ResponseEntity<TableRows> columnData(@PathVariable("selectedServer")String serverName, @PathVariable("selectedTable") String tableName)
  throws SQLException {
    System.out.println("column data requested for:" +tableName+ " on server "+ serverName);
    return new ResponseEntity<>(serversAndTablesRepository.getTableData(serverName, tableName), HttpStatus.OK);

  }

  @PostMapping(value="/insert/{selectedServer}/{selectedTable}")
  public ResponseEntity<String> insertRow(@PathVariable("selectedServer")String serverName, @PathVariable("selectedTable") String tableName, @RequestBody String json)  throws SQLException {
    JSONObject obj = new JSONObject(json);
    JSONObject row=obj.getJSONObject("data");
    String[] response=serversAndTablesRepository.insertRow(row,serverName,tableName).split("::");
    System.out.println(json);
    if (Integer.parseInt(response[0])>0)return new ResponseEntity(null,HttpStatus.OK);
    return new ResponseEntity(response[1],HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @PostMapping(value="/delete/{selectedServer}/{selectedTable}")
  public ResponseEntity<String> deleteRow(@PathVariable("selectedServer")String serverName, @PathVariable("selectedTable") String tableName, @RequestBody String json)  throws SQLException {

    JSONObject obj = new JSONObject(json);
    JSONObject row=obj.getJSONObject("data");
    String[] response=serversAndTablesRepository.deleteRow(row,serverName,tableName).split("::");
    System.out.println(json);
    if (Boolean.valueOf(response[0])==true)return new ResponseEntity(null,HttpStatus.OK);
    return new ResponseEntity(response[1],HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @PostMapping(value="/update/{selectedServer}/{selectedTable}")
  public ResponseEntity<String> updateRow(@PathVariable("selectedServer")String serverName, @PathVariable("selectedTable") String tableName, @RequestBody String json)  throws Exception {

    JSONObject obj = new JSONObject(json);
    JSONArray row=obj.getJSONArray("data");
    String[] response=serversAndTablesRepository.updateRow(row,serverName,tableName).split("::");
    System.out.println(json);
    if (Boolean.valueOf(response[0])==true)return new ResponseEntity(null,HttpStatus.OK);
    return new ResponseEntity(response[1],HttpStatus.UNPROCESSABLE_ENTITY);
  }


//  public static void main(String[] args) {
//    ServerController serverC = new ServerController();
//    serverC.serversAndTablesRepository =new ServersAndTablesRepository();
//    serverC.serversAndTablesRepository.setup();
//    serverC.serverList();
//    serverC.tableList("IL");
//    serverC.columnList("IL","IL_CONFIG");
//  }


}

