import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { RequestService } from 'src/app/services/RequestService';
import { HttpErrorResponse } from '@angular/common/http';
import { MapappComponent } from '../mapapp.component';
import { DirectionWhithWaypoints } from 'src/app/models/DirectionWithWaypoints';

import { latLon } from 'src/app/models/latLon';

@Component({
  selector: 'app-map-controll',
  templateUrl: './map-controll.component.html',
  styleUrls: ['./map-controll.component.css']
})
export class MapControllComponent implements OnInit {
  @Input() dirW : DirectionWhithWaypoints
  @Output() delDirW: EventEmitter<DirectionWhithWaypoints> = new EventEmitter();
  @Output() addDirW: EventEmitter<DirectionWhithWaypoints> = new EventEmitter();


  public oksT:boolean = true;
  public flagone:boolean = false;
  public flagtwo:boolean = false;
  public flagtree:boolean = false;

  public isRequestError:boolean = false;
  public cams: any[];

  public infosOfPoint: any;

  public origin: latLon;
  public dest: latLon;
  public wayPoints: latLon[]= [];
  public onWater:boolean ;

  constructor(private req: RequestService ,private map: MapappComponent) {
   }

  ngOnInit(): void {
  }
 
  arr: any;
  createArrFromBlob(image: Blob) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
       this.arr = reader.result;
      // console.log(reader.result);
    }, false);

    if (image) {
      reader.readAsArrayBuffer(image);
    }

  }

  startOne(){
    this.flagone=true;
    this.map.showMarker = true;
  }
  setOrigin(){
    this.getInfosOfPoint();

    if(!this.onWater){

    this.wayPoints.push( 
      {
      lat: this.map.latitudeM,
      lng: this.map.longitudeM 
    }
    );
    this.flagtwo = true;
    }
    
  }
  setWay(){
    if(!this.onWater){this.getInfosOfPoint();
    this.wayPoints.push(
      {
        lat: this.map.latitudeM,
        lng: this.map.longitudeM
      }
    );}
  }
  setDest(){
    if(!this.onWater)
    {
      this.getInfosOfPoint();
      this.wayPoints.push( 
      {
      lat: this.map.latitudeM ,
      lng: this.map.longitudeM 
    }
    );
 console.log( this.wayPoints);
 this.flagone=false;
 this.flagtwo=false;
  this.map.paintMap(this.wayPoints);}
  }

imageToShow: any;
isImageLoading:boolean = false;
createImageFromBlob(image: Blob) {
   let reader = new FileReader();
   reader.addEventListener("load", () => {
      this.imageToShow = reader.result;
      var img = new Image();
      img.src = this.imageToShow;
      var canvas = <HTMLCanvasElement> document.getElementById("mycanvas");
      var ctx = canvas.getContext("2d");
      img.onload = () => {
        ctx.drawImage(img, 0, 0)
        var pixel = ctx.getImageData(0,0,1,1);
        console.log(pixel.data[0],pixel.data[1],pixel.data[2],pixel.data[3]);
        if(pixel.data[0]==171&&pixel.data[1]==219&&pixel.data[2]==255){
          window.alert("ON WATER");
          this.onWater=true;
        }
        else{
          this.onWater=false;
        }
      }
   }, false);

   if (image) {
      reader.readAsDataURL(image);
    }
}


  getInfosOfPoint(){
    const urlSpls = 'https://maps.googleapis.com/maps/api/staticmap?center='+`${this.map.latitudeM}`+','+`${this.map.longitudeM}`+'&zoom=16&size=1x1&maptype=roadmap&sensor=false&key=AIzaSyDaxjTT7ejDx8ykQs7UU3_fuKnPLIIztjo'

    this.isImageLoading = true;
    this.req.getInfosOfPoint(urlSpls).subscribe(
      data => {
        this.createImageFromBlob(data);
        this.createArrFromBlob(data);
        this.isImageLoading = false;
      }, error => {
        this.isImageLoading = false;
        console.log(error);
      });

     return new Promise((resolve, reject) => {
        console.log('getInfosOfPoint');
        resolve();
    });
  }

setUnexploredRoute(){
  
}


}
