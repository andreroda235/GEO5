import { Component, OnInit } from '@angular/core';
import { MapappComponent } from '../mapapp.component';

import { Direction } from '../../models/Direction';
import { RequestService } from 'src/app/services/RequestService';
import { HttpErrorResponse } from '@angular/common/http';
import { Etty } from 'src/app/models/Etty';
import { locationl } from 'src/app/models/locationl';
import { FormGroup, FormControl } from '@angular/forms';
import { DirectionWhithWaypoints } from 'src/app/models/DirectionWithWaypoints';

@Component({
  selector: 'app-caminho',
  templateUrl: './caminho.component.html',
  styleUrls: ['./caminho.component.css']
})
export class CaminhoComponent implements OnInit {
  directions:Direction[];
  isFavError: boolean = false;
  loadingFav: boolean = false;
  searchWord: boolean = false;
  notEmpty: boolean = false;
  totalRecords: number=0
  page: number
  myGroup

  
  constructor(private t:MapappComponent,private req: RequestService) { }

  ngOnInit(): void {
    this.directions = this.t.waywayway;
    this.totalRecords = this.directions.length;
    this.page=1;
    this.myGroup = new FormGroup({
      data: new FormControl()
   });
  }


  searchWithKeyword(word){
   
    if(word.value.length >= 4){
     
    const body={
      search:word.value
    }

    this.req.searchWithKeyword(body).subscribe(
      (data:any)=>{
       
        data.forEach( (element: any) => {       
          this.processDataRo(element);
        });
      
        
        this.searchWord = true;
        setTimeout(()=>{this.searchWord = false; this.t.saveDirections(); word.value ="" },100);
    },
      (err : HttpErrorResponse)=>{
      
       this.searchWord = true;
       setTimeout(()=>{this.searchWord = false; word.value = ""; },2000);
      }
    );
  }else{
    this.notEmpty = true;
    setTimeout(()=>{this.notEmpty = false; word.value = ""; },2000);
  }

  }

  deleteDir(direction:Direction){
    this.directions = this.directions.filter(tr => tr !== direction);

    this.t.waywayway = this.directions;

    this.t.saveDirections();
  }

  delAll(){
    this.directions.forEach(
      (d:Direction)=> this.deleteDir(d)
    );
    this.totalRecords = 0;
  }
 
  getAllMyRoutes(){

    this.delAll();

    this.req.getmyCams().subscribe(
      (data : any )=>{  console.log(data); 
        data.forEach( (element: any) => {
          this.processDataRo(element);
          
          this.loadingFav = true;
          setTimeout(()=>{
            this.loadingFav = false
            this.t.saveDirections(); 
          }
            ,1000);
      },(err : HttpErrorResponse)=>{
        this.isFavError = true;
        });
      },(err : HttpErrorResponse)=>{
        this.isFavError = true;
        });
  }

  processDataRo(element){
    console.log(element.intermidiatePoints);
    
      var mf = new Direction();   
      
      if(element.intermidiatePoints.length != 0){
        var arr = []; 
        for(var i = 0;i<element.intermidiatePoints.length;i++){
        arr[i] ={location:{
                          lat: parseFloat(element.intermidiatePoints[i].lat),
                          lng: parseFloat(element.intermidiatePoints[i].lng)
                          },
                stopover: false, 
                };
       
        }
        mf.waypoints = arr;
      }
    
    var imgu = [];
    this.req.getRoutePhotos(element.id).subscribe(
      res  => {
        res.map(
          a=> {   
            var sr = "https://storage.cloud.google.com/apdc-geoproj.appspot.com/"+a;
            imgu.push(sr);
          }
        );

        mf.imagesS = imgu;
        mf.travelMode = "WALKING";
        mf.destination = {
        lat: parseFloat(  element.destination.lat.valueOf() ),
        lng: parseFloat(  element.destination.lng.valueOf())
        };
        mf.origin = {
        lat: parseFloat(  element.origin.lat.valueOf()) ,
        lng: parseFloat(  element.origin.lng.valueOf())
        };
        mf.id = element.id ;
        mf.title = element.title;
        mf.description = element.description;             
        this.t.waywayway.push(mf);
        
        

      },(err : HttpErrorResponse)=>{ console.log(err); });
    
  }

}