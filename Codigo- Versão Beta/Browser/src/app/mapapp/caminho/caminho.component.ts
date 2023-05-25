import { Component, OnInit } from '@angular/core';
import { MapappComponent } from '../mapapp.component';

import { Direction } from '../../models/Direction';
import { RequestService } from 'src/app/services/RequestService';
import { HttpErrorResponse } from '@angular/common/http';
import { Etty } from 'src/app/models/Etty';
import { locationl } from 'src/app/models/locationl';

@Component({
  selector: 'app-caminho',
  templateUrl: './caminho.component.html',
  styleUrls: ['./caminho.component.css']
})
export class CaminhoComponent implements OnInit {
  directions:Direction[];
  isFavError: boolean = false;
  loadingFav: boolean = false;
  constructor(private t:MapappComponent,private req: RequestService) { }

  ngOnInit(): void {
    this.directions = this.t.waywayway;    
  }

  showHide1(){ 
    console.log("Fered");
    
  }
  

  deleteDir(direction:Direction){
    this.directions = this.directions.filter(tr => tr !== direction);

    
    this.t.waywayway = this.directions;

    this.t.saveDirections();

  }

 
  getAllMyRoutes(){
    this.req.getmyCams().subscribe(
  
    (data : any )=>{
    
    data.forEach( (element: Etty) => {
    
    if(element.type){  
    const mf = new Direction();
    mf.travelMode = "WALKING";
    mf.destination = 
    {
      lat: parseFloat(  element.destination.lat.valueOf() ),
      lng: parseFloat(  element.destination.lng.valueOf())
    };
    mf.origin = 
    {
      lat: parseFloat(  element.origin.lat.valueOf()) ,
      lng: parseFloat(  element.origin.lng.valueOf())
    };
    
    mf.id = this.t.create_UUID();

    mf.title = element.title;
    mf.description = element.description;
    
      var arr: [{location:{lat:number,lng:number}}] = [{location:{lat:0,lng:0}}];
      

      for(var i = 0;i<element.intermidiatePoints.length;i++){
        
        arr[i] = 
        {location:{
          lat: parseFloat(element.intermidiatePoints[i].lat),
          lng: parseFloat(element.intermidiatePoints[i].lng)} 
        };

      }
      mf.waypoints = [{
        location:
        { 
            lat:0,
            lng:0
        }
      }];
      mf.waypoints = arr; 
      
     this.t.waywayway.push(mf);
    }
    else{
      const ms = new Direction();
      ms.travelMode = "WALKING";
      ms.destination = 
    {
      lat: parseFloat(  element.destination.lat.valueOf() ),
      lng: parseFloat(  element.destination.lng.valueOf())
    };
    ms.origin = 
    {
      lat: parseFloat(  element.origin.lat.valueOf()) ,
      lng: parseFloat(  element.origin.lng.valueOf())
    };
    
    this.t.waywayway.push(ms);
    }
    
    
    });
  
    this.t.saveDirections();
    this.loadingFav = true;
    setTimeout(()=>this.loadingFav = false,1000);
},
(err : HttpErrorResponse)=>{
 this.isFavError = true;
});
}

}
