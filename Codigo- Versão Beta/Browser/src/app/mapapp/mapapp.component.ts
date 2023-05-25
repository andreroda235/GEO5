import { Component, OnInit, ViewChild, ElementRef, NgZone ,Renderer2, ComponentFactoryResolver, Output, EventEmitter, Input } from '@angular/core';
import { AgmCoreModule, MapsAPILoader, MouseEvent, AgmMap, AgmMarker } from '@agm/core';
import { Url } from 'url';
import { TranslateService } from '@ngx-translate/core';
import { NgForm,FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { Direction } from '../models/Direction';
import { DirectionWhithWaypoints } from '../models/DirectionWithWaypoints';
import { Router } from '@angular/router';
import {  HostListener } from '@angular/core';
import { InfoWindow } from '@agm/core/services/google-maps-types';
import { BehaviorSubject } from 'rxjs';


@Component({
  selector: 'app-mapapp',
  templateUrl: './mapapp.component.html',
  styleUrls: ['./mapapp.component.css']
})

export class MapappComponent implements OnInit {
  @Output() newMF: EventEmitter<Direction> = new EventEmitter();
  @Output() showHide: EventEmitter<String> = new EventEmitter();
  
  maptype:String; latitude: number; longitude: number; zoom:number; l: string;previous ; Options ;
   public show: boolean = false;
  srt: any;
  latitudeM: number; longitudeM: number;
  showMarker:boolean = false;
  imagesRandom:randomImages[];
  onMainPage:boolean;
  str; formDir ;
  public infoOptionLat: any
  public infoOptionLng: any
  
  public thisInfoW: InfoWindow = undefined;
  public show2: boolean = false;
  
  private messageSource = new BehaviorSubject('default message');
  currentMessage = this.messageSource.asObservable();

  private messageSourceClose = new BehaviorSubject('default message');
  currentMessageClose = this.messageSourceClose.asObservable();

  constructor(
    private router : Router,
    public translate: TranslateService,
  )
  {  
    
      this.formDir = new FormGroup(
        {
          travelMode: new FormControl("WALKING"),
          origin: new FormGroup ({lat: new FormControl(), lng: new FormControl()}),
          destination:new FormGroup ({lat: new FormControl(),lng: new FormControl()}),
          visible: new FormControl(true)
        }
      );
  }
  cc = [];
  ccOF:boolean = false;
  ngOnInit(): void {
    this.setCurrentLocation();
    
    this.l = localStorage.getItem('language');
    if(this.l==null){
      this.l='pt';
    } 

    localStorage.setItem('language',this.l);
    
    this.str = localStorage.getItem("onMainPage");

    if( this.srt === null || this.str =="true"){
      this.ChangeOnMainPageT();
    }
    else{
      this.ChangeOnMainPageF();
    }

    this.loadDirections();
    this.getDirection();
   
    this.translate.setDefaultLang(localStorage.getItem('language'));
   // this.getDirection();
   // this.req.getTodos().subscribe(reqw => {
   //   this.imagesRandom = reqw;
      //reqw.forEach(randomImages, index: number, array: randomImages[])
   //   console.log(reqw);
   // });
  
  }

  showHMarker($event: MouseEvent){
    this.latitudeM = $event.coords.lat;
    this.longitudeM = $event.coords.lng;

 
     this.showMarker = !this.showMarker ; 
    
  }

  public removeDirection(){
    this.show = false
  }

  hideRoutes(){
    this.waywayway.map(
      tr=>  {
       // tr.visible = false;
        this.messageSourceClose.next(tr.id.toString());
      }
    );
  }
  
  public showDirection(){
    this.dirWaysPollyP = JSON.parse(localStorage.getItem("mySpecialDir"));
   
    setTimeout(()=>{
    this.dirWaysPollyP.forEach(
      (th:any[])=>
      {
        
        this.cc.push(
          {
            latitude: parseFloat(th[1]),
            longitude: parseFloat(th[0])
          }
        );}
      
    );


     this.show = true 
    
     this.latitude = this.cc[0].latitude;
     this.longitude = this.cc[0].longitude;     

    },
     300);
    
  }
  

  clickedMarker(infowindow) {
    if (this.previous) {
        this.previous.close();
    }
    this.previous = infowindow;
  }

  clickedDirection(DirMrk:AgmMarker){    
     
    var v = DirMrk.id.toString();

   
      this.hideRoutes();
      
   
    this.messageSource.next(v);
     
    this.ChangeOnMainPageF();
   
    this.router.navigate(['/map/caminho']);
    
  }



  ChangeOnMainPageT(){
    localStorage.setItem("onMainPage","true");
    this.onMainPage = true;
  }
  ChangeOnMainPageF(){
    localStorage.setItem("onMainPage","false");
    this.onMainPage = false;
  }

  loadDirections(){
    const st = localStorage.getItem("myDirections");
    if(st==null){
      this.waywayway = [];
    }
    else this.waywayway = JSON.parse(st);

  }
  saveDirections(){
    localStorage.setItem("myDirections", JSON.stringify(this.waywayway));
  }

 //right click display Options
  getOptions($event: MouseEvent, inf){  
  this.infoOptionLat = $event.coords.lat;
  this.infoOptionLng = $event.coords.lng;
  if(this.show2){
    inf.close();
  }else inf.open();
}

// Get Current Location Coordinates
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
      this.latitude = 38.660109;
      this.longitude = -9.203209;
      this.latitudeM = 38.660109;
      this.longitudeM = -9.203209;

      this.zoom = 8;
  
} else{
  this.latitude = parseFloat(posMLat);
  this.longitude = parseFloat(posMLng);
  this.latitudeM = parseFloat(posMLat);
  this.longitudeM = parseFloat(posMLng);

  this.zoom = 8;

  }
}

markerDragEnd($event: MouseEvent) {
  this.latitudeM = $event.coords.lat;
  this.longitudeM = $event.coords.lng;
  localStorage.setItem("posMLat",this.latitudeM.toString());
  localStorage.setItem("posMLng",this.longitudeM.toString());
}

getDirection() {
  this.travelMode = "WALKING" //WALKING TRANSIT BICYCLING DRIVING 
  this.origin = { lat: 38.661076, lng: -9.205908 }
  this.destination = { lat: 38.66250759275842, lng: -9.160076401382238 }
  this.waypoints = [
    {location: {lat: 38.664092,lng: -9.196742}},
    {location: {lat: 38.661202,lng: -9.185289}}
  ]
}
// this is an Start of Routine Define new Way
newDefInc=true
newDef1=false;
newDef2=false;

definirCaminho(){
  this.showMarker = true;
  this.waypoints = [];
  setTimeout( () => this.newDefInc =false ,500);
  
}
//Def way 2nd part
Title = "";
Description = "";
definirC1(Title,Description){
  this.Title = Title;
  this.Description = Description;
    this.origin = { lat: this.latitudeM, lng: this.longitudeM};
    this.markers.push(
      {
      latitude:this.latitudeM,
      longitude:this.longitudeM,
      info:`Start at ${this.latitudeM} and ${this.longitudeM}`,
      label:"S",
      iconUrl:"http://maps.google.com/mapfiles/ms/icons/red-dot.png"
     }
    );
    this.newDef1=true;
  
}

definirC2(){

  this.destination = { lat: this.latitudeM, lng: this.longitudeM };
  this.markers.push(
    {
    latitude:this.latitudeM,
    longitude:this.longitudeM,
    info:`Finish at ${this.latitudeM} and ${this.longitudeM}`,
    label:"F",
    iconUrl:"http://maps.google.com/mapfiles/ms/icons/red-dot.png"
   }
  );
  this.newDef2 = true;
}

wayPushed:boolean=false;
anyWayP:boolean=false;
definirC2WayP(){
  var mf = {
    location: {
      lat: this.latitudeM, 
      lng: this.longitudeM
    }
  }

  this.markers.push(
    {
    latitude:this.latitudeM,
    longitude:this.longitudeM,
    info:`this marker is at ${this.latitudeM} and ${this.longitudeM}`,
    label:"I",
    iconUrl:"http://maps.google.com/mapfiles/ms/icons/blue-dot.png"
    
   }
  );
  

  this.waypoints.push(mf);

  this.wayPushed = true;
  this.anyWayP = true;
  setTimeout(()=>this.wayPushed=false,800);
}

create_UUID(){
  var dt = new Date().getTime();
  var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      var r = (dt + Math.random()*16)%16 | 0;
      dt = Math.floor(dt/16);
      return (c=='x' ? r :(r&0x3|0x8)).toString(16);
  });
  return uuid;
}

finalizeSetDir(){
this.markers = [];  
var mf;
if(this.anyWayP){
   mf = new DirectionWhithWaypoints();
   mf.waypoints = this.waypoints;
}else{
   mf = new Direction();
}
   
  mf.username = localStorage.getItem("username");
  
  mf.title = this.Title;
  mf.description = this.Description;

  mf.travelMode="WALKING";
  mf.origin = this.origin;
  mf.destination = this.destination;
  mf.id = this.create_UUID();
  mf.type = this.anyWayP;
  mf.visible = false;

  console.log(mf);
 
 this.waywayway.push(mf);
 
 this.saveDirections();
  
 this.abortChanges();

 this.ChangeOnMainPageF();

 this.router.navigate(['/map/caminho']);

 

}

////////////////////////////////////////////////
dirWaysP:any[]=[];
newPolly:boolean=false;
paintMap(dirW){

  this.dirWaysP = dirW;
  setTimeout(
    ()=> this.newPolly=true, 1000
  );
}
//////////////////////////////////////////////////
abortChanges(){
  this.newDefInc=true
  this.newDef1=false;
  this.newDef2=false;
}

onSubmit(myForm){

 const latlongO = new google.maps.LatLng(parseFloat(myForm.origin.lat),parseFloat(myForm.origin.lng));
 const latlongD = new google.maps.LatLng(parseFloat(myForm.destination.lat),parseFloat(myForm.destination.lng));

 const mf = new Direction(); 
 mf.username = localStorage.getItem("username");
 mf.title = "test_Title";
 mf.description = "test_Description";
  mf.travelMode="WALKING";
  mf.origin.lat = parseFloat(myForm.origin.lat);
  mf.origin.lng = parseFloat(myForm.origin.lng);
  mf.destination.lat = parseFloat(myForm.destination.lat);
  mf.destination.lng = parseFloat(myForm.destination.lng);
  mf.visible = false;
 

 this.waywayway.push(mf);
 

 this.saveDirections();

  
 this.abortChanges();
}

newDirectionAdded(b:boolean){
  if(b){
    
    this.showDirection();
  }
}
public showNaturalReserve(){
  this.dirWaysPollyP = JSON.parse(localStorage.getItem("ShowNaturalP"));
 this.cc = [];
 
  setTimeout(()=>{
  this.dirWaysPollyP.forEach(
    (th:any[])=>
    {
      
      this.cc.push(
        {
          lat: parseFloat(th[1]),
          lng: parseFloat(th[0])
        }
      );}
    
  );
  
  this.dirPolygon.push(this.cc);

  this.show = true 
  
   this.latitude = this.cc[0].lat;
   this.longitude = this.cc[0].lng;     

  },
   600);
  
}
newNatResevreAdded(b:boolean){
  if(b){
    
    this.showNaturalReserve();
  }
}
public waywayway: Direction[];
/*
public waywayway = [
{
  travelMode:"WALKING",
  origin:{lat: 42.030742, lng: -8.1594},
  destination:{lat: 42.031645,lng: -8.161525},
  waypoints:[
    {location: {lat: 42.03107,lng: -8.160093}},
    {location: {lat: 42.031072,lng: -8.161099}}
  ],
  visible:true
},
{
  travelMode:"WALKING", //WALKING TRANSIT BICYCLING DRIVING 
  origin:{ lat: 38.661076, lng: -9.205908 },
  destination: { lat: 38.66250759275842, lng: -9.160076401382238 },
  waypoints : [
    {location: {lat: 38.664092,lng: -9.196742}},
    {location: {lat: 38.661202,lng: -9.185289}}
  ],
  visible:true
}
]
*/
//////////////////////////////////////////////////
public origin: any
public destination: any
public travelMode: any
public waypoints: any

markers = [
 // {latitude: 38.657849552573595, longitude: -9.177789709716588,info:'this is 1'}, 
  //{latitude: 38.6494375039336, longitude: -9.163289687782079,info:'this is 2'},
 // {latitude: 38.66250759275842, longitude: -9.160076401382238,info:'this is 3'}
 
]  
public slides = [
  { src: "https://s1.1zoom.me/big0/703/Planets_Trees_Night_576489_1280x800.jpg" },
  { src: "https://s1.1zoom.me/big0/324/USA_Coast_Oregon_coast_sea_Crag_Trees_576509_1280x791.jpg" },
  { src: "https://s1.1zoom.me/big0/205/Greece_Sunrises_and_sunsets_Coast_Korfu_Crag_Rays_575551_1280x853.jpg" },
  { src: "https://s1.1zoom.me/big0/307/Forests_Autumn_Trees_Rays_of_light_575453_1280x720.jpg" }
];



public renderOptions = {
  suppressMarkers: true,
}

public markerOptions = {
  origin: {
      icon: {url: '../../assets/svg/flag-checkered-solid.svg' // atençao svg alterado para width="50" height="50"
    }
  },
  destination: {
      icon: { }
     
  },
}


dirWaysPollyP = [   ]; // [{},....]

dirPolygon = [ //[ [{},..], [{},..], ... ]
  [
  
  ]
]
  ;

reservaNatural = [
  {
    title:"Paisagem Protegida da Arriba Fóssil da Costa de Caparica",
    coord:{lat:41.3698,lng:7.81768},
    },
    {
    title:"Parque Natural da Arrábida", 
    coord:{lat:38.468341,lng:-9.064629},
    },
    {
    title:"Reserva Natural do Estuário do Sado",
    coord:{lat:38.526465,lng:-8.803120},
    },
    {
    title:"Reserva Natural do Estuário do Tejo",
    coord:{lat:38.827208,lng:-8.988728},
    },
    {
    title:"Parque Natural de Sintra-Cascais",
    coord:{lat:38.793941,lng:-9.434548},
    },
    {
    title:"Parque Natural do Sudoeste Alentejano e Costa Vicentina",
    coord:{lat:37.456909,lng:-8.790526},
    },
    {
    title:"Parque Natural do Vale do Guadiana",
    coord:{lat:37.645547,lng:-7.668150},
    },
    {
    title:"Parque Natural da Serra de São Mamede",
    coord:{lat:39.310489,lng:-7.410112},
    },
    {
    title:"Reserva Natural do Paul do Boquilobo",
    coord:{lat:39.389310,lng:-8.539366},
    },
    {
    title:"Reserva Natural do Paul de Arzila",
    coord:{lat:40.182975,lng:-8.548877},
    },
    {
    title:"Serra da Estrela Natural Park",
    coord:{lat:40.330232,lng:-7.627111},
    },
    {
    title:"Reserva Natural da Serra da Malcata",
    coord:{lat:40.276118,lng:-7.030297},
    },
    {
    title:"Parque Natural do Douro Internacional",
    coord:{lat:41.271091,lng:-6.652391},
    },
    {
    title:"Parque Natural do Alvão",
    coord:{lat:41.367130,lng:-7.820801},
    },
    {
    title:"Parque Natural do Litoral Norte",
    coord:{lat:41.523038,lng:-8.791700},
    },
    {
    title:"Parque Nacional Peneda-Gerês",
    coord:{lat:41.839496,lng:-8.242081},
    },
    {
    title:"Parque natural de Montesinho",
    coord:{lat:41.905205,lng:-6.862013},
    },
    {
    title:"Parque Natural das Serras de Aire e Candeeiros",
    coord:{lat:39.502818,lng:-8.812173},
    },
    {
    title:"Parque Natural da Madeira",
    coord:{lat:32.757373,lng:-16.995811},
    }
];

 
}
export class randomImages {
      id : number;
      author: string;
      width: number;
      height: number;
      url: Url;
      download_url: Url
}