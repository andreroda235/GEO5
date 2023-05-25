import {Component, ElementRef, ViewChild, Output, EventEmitter} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import { Router } from '@angular/router';
import { MouseEvent, LatLng} from '@agm/core';
//import { AgmCoreModule ,MouseEvent, LatLng} from '@agm/core';
//import { BrowserModule } from '@angular/platform-browser';
//import { numberFormat } from 'highcharts';
//import  Utm  from 'geodesy/utm.js';
import  Dms  from 'geodesy/dms.js';
import { RequestService } from 'src/app/services/RequestService';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-map-management',
  templateUrl: './map-management.component.html',
  styleUrls: ['./map-management.component.css']
})
export class MapManagementComponent {

  //Copy paste do Form do mapa...

  @Output() newDirectionAdded: EventEmitter<boolean> = new EventEmitter();
  @Output() moved: EventEmitter<LatLng> = new EventEmitter();
  @ViewChild('labelImport')
  labelImport: ElementRef;

  formImport: FormGroup;
  fileToUpload: File = null;

  titleDesc:FormGroup;
  title :string;
  description: string;
  Tags:string;

  markerDragable = true;
  descriptionChanged = true;
  imagesAddedtoSend = true;

  latitude: number; longitude:number; latitudeM: number; longitudeM: number; zoom:number;

  constructor(private router:Router , private req:RequestService) {
    this.formImport = new FormGroup({
      importFile: new FormControl('', Validators.required)
    });
  }

  ngOnInit(): void {
    this.setCurrentLocation();
    

  }
  onFileChange(files: FileList) {
    this.labelImport.nativeElement.innerText = Array.from(files)
      .map(f => f.name)
      .join(', ');
    this.fileToUpload = files.item(0);
  }

  import(){
    
  }


  private setCurrentLocation() {
    const posMLat = localStorage.getItem("posMLat");
    const posMLng = localStorage.getItem("posMLng");
    if(posMLat==null){
   /* if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition((position) => {
        this.latitude = position.coords.latitude;
        this.longitude = position.coords.longitude;
        this.latitudeM = position.coords.latitude;
        this.longitudeM = position.coords.longitude;
  
        this.zoom = 15;
      });
    }else { 
      */
        this.latitudeM = 38.660109;
        this.longitudeM = -9.203209;
        this.latitude = 38.660109;
        this.longitude = -9.203209;

        this.zoom = 8;
    
  } else{
    this.latitudeM = parseFloat(posMLat);
    this.longitudeM = parseFloat(posMLng);
    this.latitude = parseFloat(posMLat);
    this.longitude = parseFloat(posMLng);
    this.zoom = 8;
  
    }
  }

  markerDragableChange(){    
    this.markerDragable = !this.markerDragable;
    console.log("changed to "+this.markerDragable)
  }

  descriptionChange(){
    this.descriptionChanged = !this.descriptionChanged;
    console.log("changed to "+this.descriptionChanged)
  }

  imagesAddedtoSendChange(){
    this.imagesAddedtoSend = !this.imagesAddedtoSend;
    console.log("changed to "+this.imagesAddedtoSend)
  }

  
  setTitDesc(Title,Description,Tags){
    this.title = Title.value;
    this.description = Description.value;
    this.Tags = Tags.value;
  }

  markerDragEnd($event: MouseEvent) {
    this.latitudeM = $event.coords.lat;
    this.longitudeM = $event.coords.lng;
    localStorage.setItem("posMLat",this.latitudeM.toString());
    localStorage.setItem("posMLng",this.longitudeM.toString()); 
  }
  getlatLngF(Latitude,Longitude){

    if(Latitude!=null&&Longitude!=null){
     
    this.latitudeM = Latitude.value;
    this.longitudeM = Longitude.value;
    this.latitude = Latitude.value;
    this.longitude = Longitude.value;  
    localStorage.setItem("posMLat",this.latitudeM.toString());
    localStorage.setItem("posMLng",this.longitudeM.toString());
    }
  }

  getLatLngDms(N,W){
    
    const Ndms = Dms.parse(N.value);
    const Wdms = Dms.parse(W.value);

    this.latitudeM = Ndms;
    this.longitudeM = Wdms;
    this.latitude = Ndms;
    this.longitude = Wdms;  
    localStorage.setItem("posMLat",this.latitudeM.toString());
    localStorage.setItem("posMLng",this.longitudeM.toString());

  }

  getlatLng($event){
    var input = $event;
    //var inp =<HTMLInputElement> document.getElementById("place");
    var autocomplete = new google.maps.places.Autocomplete(input);
    var place;
    autocomplete.addListener('place_changed', function() {
      place = autocomplete.getPlace();
      if(place.geometry){
        localStorage.setItem("posMLat",place.geometry.location.lat());
        localStorage.setItem("posMLng",place.geometry.location.lng());
      }
     
    });

    this.setCurrentLocation();
  }

  imageToShow:any;
  public imagePath;
  imgURL: any;
  images:any[];
  imageSend=false;
  filesToUpload: FileList = null;


  onFileChangeS(files: FileList) {
    this.labelImport.nativeElement.innerText = Array.from(files)
      .map(f => f.name)
      .join(', ');
    this.filesToUpload = files;
  }



  importImages():void{

    var reader = new FileReader();
    this.imagePath = this.filesToUpload;

    reader.readAsDataURL(this.filesToUpload[0]); 
    reader.onload = (_event) => { 
      this.imgURL = reader.result; 
    }

    const arrayOfBase64 = fileListToBase64(this.filesToUpload);
   
    arrayOfBase64.then(data => {
      this.images = data;
      this.imageSend = true;
      
  });
  }


  SubmitGeoStop(){
    
   
    //var images = this.filesToUpload[0];

    var toS:toSend ={
      geoSpotName:this.title,
      description:this.description,
      tags: this.Tags,
      location:{
        lat:this.latitudeM,
        lng:this.longitudeM
      }
    }
   
    this.req.submitGeoSpot(toS).subscribe(
      data=>{
        var i = 0;
        while(i<this.filesToUpload.length){
    
        try{
        this.req.addGeoSpotPhoto(this.title,this.filesToUpload[i],this.filesToUpload[i].type).subscribe(
          (data)=>{
            console.log("image ok "+i);
          },
          (err : HttpErrorResponse)=>{
            window.alert("Imagens nao adicionados!  Images Not Added!");
            console.log(err);
          });
        }catch(err){ }
          i++;
        }

        window.alert("GeoSpot Adicionado!  GeoSpot Added!");
      },
      (err : HttpErrorResponse)=>{ window.alert("Não Adicionado!   Not Added!") }
    );

    console.log(toS);
  }

  newInfoNotice( Title, Description, GeoMapLink, ExplicativeNotice){
    if(Title!=null && Description!=null && GeoMapLink!=null && ExplicativeNotice!=null){
      var th = {
        title: Title.value,
        description: Description.value,
        mapLink: GeoMapLink.value,
        noticeLink: ExplicativeNotice.value,
        location:{
          lat:this.latitudeM,
          lng:this.longitudeM
        } 
      }

      this.req.submitInfoRes(th).subscribe(
        data=>{
          var i = 0;
        while(i<this.filesToUpload.length){
    
        try{
        this.req.addInfoResPhoto(Title.value,this.filesToUpload[i],this.filesToUpload[i].type).subscribe(
          (data)=>{
            console.log("image ok "+i);
          },
          (err : HttpErrorResponse)=>{
            window.alert("Imagens nao adicionados!  Images Not Added!");
            console.log(err);
          });
        }catch(err){ }
          i++;
        }
        window.alert("Informações Adicionadas!  Information Added!");
        }
      );


    }
  }


}
export interface toSend{
    geoSpotName:string,
    description:string,
    tags:string,
    location:{
       lat:number,
       lng:number
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
