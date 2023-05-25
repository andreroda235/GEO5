import { Component, OnInit ,Input, EventEmitter, Output, Directive} from '@angular/core';
import { RequestService } from '../../../services/RequestService';
import { Direction } from '../../../models/Direction';
import { HttpErrorResponse } from '@angular/common/http';
import {  HostListener } from '@angular/core';
import { MapappComponent } from '../../mapapp.component';
import { NgbCarouselConfig } from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'app-direction',
  templateUrl: './direction.component.html',
  styleUrls: ['./direction.component.css']
})
export class DirectionComponent implements OnInit {
  @Input() direction : Direction
  @Output() deleteDir: EventEmitter<Direction> = new EventEmitter();
  isRequestError: boolean = false;
  isRequestSuccess: boolean = false;
  isImageSendOK: boolean = false;

  activeIds: string[] = [];
  dirId:string;
  images
  imagesS
  flag= false;
  constructor(private rr:MapappComponent, private req: RequestService, config: NgbCarouselConfig) {
    // customize default values of carousels used by this component tree
    config.interval = 60000;
    config.wrap = false;
    config.keyboard = false;
    config.pauseOnHover = false;
  }

  imagesize = false;
  bigImage(){
    this.imagesize = true; 
    this.rr.showImageCarosel(this.images);
  }

  bidImageS(){
    this.imagesize = true; 
    this.rr.showImageCar(this.imagesS);
  }

  ngOnInit(): void {
    
    this.rr.currentMessage.subscribe(
      dirId=>{
        if(dirId==this.direction.id){
          this.dirId=dirId;
          this.activeIds = ["id12"];
          this.direction.visible = true;
        }
         
      }
      
      );
      this.rr.currentMessageClose.subscribe(
        dirId=>{
          if(dirId==this.direction.id){
            this.dirId=dirId;
            this.activeIds = [];
            this.direction.visible = false;
          }
           
        }
      );
    
      var as = [{location: {lat: 0, lng: 0},stopover: false}]
      var loca = this.direction.origin;
      

      if(this.direction.images != null){ 
   
      const arrayOfBase64 = fileListToBase64(this.direction.images);
      arrayOfBase64.then(data => {
      this.flag = true;  
      this.images = data;    
      });
      }else{
        this.imagesS = this.direction.imagesS 
      }
  }

  setClasses(){
    let classes = {
        direction: true,
        'visible': this.direction.visible,
        stopover: false
    }
    return classes
  }

  getImages(){
    var resp=[];
    this.req.getRoutePhotos(this.direction.id).subscribe(
      res  => {
        //console.log(res)
        res.map(
          a=> {
           // console.log(a.properties.file_name.value);
           var img = new Image();
           img.src = "https://storage.cloud.google.com/apdc-geoproj.appspot.com/"+a.properties.file_name.value;
           resp.push(img);
          }
        );
        const arrayOfBase64 = fileListToBase64(resp);
        arrayOfBase64.then(data => {
        this.images = data;
        this.flag = true;
        });
      },
      (err : HttpErrorResponse)=>{
      console.log(err);
      }
    );


  }

  delete(direction){
    this.deleteDir.emit(direction);
  }


  showHide(){
    this.direction.visible = !this.direction.visible;
  }
 

  addtoFavorites(direction:Direction){
    var dir = new Direction();
    dir.id = direction.id;
    dir.description = direction.description;
    dir.destination = direction.destination;
    dir.origin = direction.origin;
    dir.title = direction.title;
    dir.travelMode = direction.travelMode;
    dir.username = direction.username;
    dir.isTracked = false;
    
    if(direction.type){
      dir.intermidiatePoints = [];
      for(var i=0;i<direction.waypoints.length;i++){
        dir.intermidiatePoints[i] = direction.waypoints[i].location;
      }
    }
    if(direction.username == localStorage.getItem("username") && !direction.createdlocally){
      //route owner
      window.alert("Caminho que tenta adicionar ja existe nos seu caminhos");

    }else{
    direction.username = localStorage.getItem("username");
    dir.id = this.rr.create_UUID();
    //console.log(dir);
    this.req.addToFovorites(dir).subscribe(
      (data : any)=>{
        this.isRequestSuccess = true;
        setTimeout( () => this.isRequestSuccess = false , 3000 );

        if(this.direction.images!=null )  this.addphoto(dir.id);
      },
      (err : HttpErrorResponse)=>{
        this.isRequestError = true;
        setTimeout( () => this.isRequestError = false , 2500 );     
      });
    }

  }

  addphoto(id){
    var i = 0;
    while(i<this.direction.images.length){

    try{
    this.req.addRoutePhoto(id,this.direction.images[i],this.direction.images[i].type).subscribe(
      (data)=>{
        console.log("image ok "+i);
      },
      (err : HttpErrorResponse)=>{
      console.log(err);
      });
    }catch(err){ }
      i++;
    }
    
  }
}

export async function fileListToBase64(fileList) {
  // create function which return resolved promise
  // with data:base64 string
  function getBase64(file) {
    const reader = new FileReader()
    return new Promise(resolve => {
      reader.onload = ev => {
        resolve(ev.target.result)
      }
      reader.readAsDataURL(file)
    })
  }
  // here will be array of promisified functions
  const promises = []

  // loop through fileList with for loop
  for (let i = 0; i < fileList.length; i++) {
    promises.push(getBase64(fileList[i]))
  }

  // array with base64 strings
  return await Promise.all(promises)
}