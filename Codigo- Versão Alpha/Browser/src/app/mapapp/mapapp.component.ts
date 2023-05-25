import { Component, OnInit, ViewChild, ElementRef, NgZone ,Renderer2, ComponentFactoryResolver } from '@angular/core';
import { AgmCoreModule, MapsAPILoader, MouseEvent, AgmMap } from '@agm/core';
import { Url } from 'url';
import { RequestService } from '../services/RequestService';
import { TranslateService } from '@ngx-translate/core';
import { NgForm,FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { Direction } from '../models/Direction';
import { DirectionWhithWaypoints } from '../models/DirectionWithWaypoints';
import { latLon } from '../models/latLon';


@Component({
  selector: 'app-mapapp',
  templateUrl: './mapapp.component.html',
  styleUrls: ['./mapapp.component.css']
})

export class MapappComponent implements OnInit {
  maptype:String  
  latitude: number
  longitude: number
  zoom:number;
  previous
  Options
  public show: boolean = false
  srt: any;
  latitudeM: number;
  longitudeM: number;
  showMarker:boolean = false;
  imagesRandom:randomImages[];
  onMainPage:boolean;
  str;
  formDir ;
  public infoOptionLat: any
  public infoOptionLng: any
 
  public show2: boolean = false;
  constructor(
    private formBuilder:FormBuilder,
    private req:RequestService,  
    public translate: TranslateService
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
    this.translate.addLangs(['pt','en']); 
    this.translate.setDefaultLang(localStorage.getItem('language'));
    
   
    this.dirWaysPollyP.forEach(
      (th:any[])=>
      {
        this.cc.push(
          {
            latitude: parseFloat(th[1]),
            longitude: parseFloat(th[0])
          }
        );
      }
    );
    
    this.str = localStorage.getItem("onMainPage");

    if( this.srt === null || this.str =="true"){
      this.ChangeOnMainPageT();
    }
    else{
      this.ChangeOnMainPageF();
    }

    this.loadDirections();
    this.getDirection();
    
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
  public showDirection(){
    this.show = true
  }
  

  clickedMarker(infowindow) {
    if (this.previous) {
        this.previous.close();
    }
    this.previous = infowindow;
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
  if ('geolocation' in navigator) {
    navigator.geolocation.getCurrentPosition((position) => {
      this.latitude = position.coords.latitude;
      this.longitude = position.coords.longitude;
      this.latitudeM = position.coords.latitude;
      this.longitudeM = position.coords.longitude;

      this.zoom = 15;
    });
  }
} else{
  this.latitude = parseFloat(posMLat);
  this.longitude = parseFloat(posMLng);
  this.latitudeM = parseFloat(posMLat);
  this.longitudeM = parseFloat(posMLng);

  this.zoom = 15;

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
definirC1(){
    
    this.origin = { lat: this.latitudeM, lng: this.longitudeM};
    this.newDef1=true;
  
}

definirC2(){
  this.destination = { lat: this.latitudeM, lng: this.longitudeM };
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
  
  this.waypoints.push(mf);

  this.wayPushed = true;
  this.anyWayP = true;
  setTimeout(()=>this.wayPushed=false,600);
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

anyWaypoints(){
var mf;
if(this.anyWayP){
   mf = new DirectionWhithWaypoints();
   mf.waypoints = this.waypoints;
}else{
   mf = new Direction();
}
   
  mf.username = localStorage.getItem("username");
  mf.title = "test_Title";
  mf.description = "test_Description";
  mf.travelMode="WALKING";
  mf.origin = this.origin;
  mf.destination = this.destination;
  mf.id = this.create_UUID();
  mf.type = this.anyWayP;

  console.log(mf.id);

  
 this.waywayway.push(mf);
 

 this.saveDirections();

  
 this.abortChanges();

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


dirWaysPollyP = [
          [-8.1594,42.030742,944.533],
          [-8.160093,42.03107,944.823],
          [-8.161099,42.031072,955.315],
          [-8.161525,42.031645,956.907],
          [-8.162029,42.031818,962.172],
          [-8.163088,42.032625,975.567],
          [-8.164032,42.033563,977.948],
          [-8.164698,42.033823,973.863],
          [-8.165476,42.03347,965.964],
          [-8.165836,42.03317,964.982],
          [-8.16606,42.032721,965.599],
          [-8.166948,42.032157,960.673],
          [-8.167355,42.031673,961.285],
          [-8.167481,42.030692,966.513],
          [-8.167984,42.02983,973.307],
          [-8.16812,42.029324,978.156],
          [-8.168021,42.028485,988.941],
          [-8.167729,42.028084,995.832],
          [-8.167717,42.027532,999.737],
          [-8.167453,42.027164,1002.868],
          [-8.167584,42.026335,1004.916],
          [-8.167906,42.025723,1009.143],
          [-8.167844,42.024522,1005.295],
          [-8.1691,42.022789,1007.549],
          [-8.168897,42.022456,1007.423],
          [-8.168823,42.02193,1006.307],
          [-8.169097,42.021356,1003.548],
          [-8.169354,42.02004,990.013],
          [-8.169628,42.019634,985.544],
          [-8.16983,42.01861,972.919],
          [-8.169592,42.01831,973.06],
          [-8.170177,42.018334,963.598],
          [-8.170449,42.018034,957.195],
          [-8.170583,42.017574,947.153],
          [-8.170426,42.01689,936.204],
          [-8.170962,42.016365,923.414],
          [-8.171172,42.015866,915.307],
          [-8.171371,42.015786,916.624],
          [-8.171361,42.015486,912.849],
          [-8.17175,42.014962,918.51],
          [-8.171922,42.013531,903.995],
          [-8.172378,42.013255,906.146],
          [-8.173066,42.01239,908.454],
          [-8.172287,42.011643,875.461],
          [-8.172233,42.011078,864.582],
          [-8.17184,42.01057,855.143],
          [-8.17189,42.010338,852.317],
          [-8.172178,42.010151,851.507],
          [-8.171719,42.01008,847.997],
          [-8.171402,42.009821,842.192],
          [-8.170945,42.009907,839.223],
          [-8.169955,42.009489,821.538],
          [-8.169022,42.008636,807.979],
          [-8.169268,42.007872,800.547],
          [-8.170569,42.007579,801.774],
          [-8.170865,42.007288,803.575],
          [-8.171279,42.007323,810.428],
          [-8.171393,42.007099,814.32],
          [-8.171229,42.007118,810.407],
          [-8.171071,42.006762,820.396],
          [-8.170816,42.006717,814.981],
          [-8.170203,42.006246,809.152],
          [-8.16936,42.005164,795.067],
          [-8.169092,42.004335,790.838],
          [-8.16906,42.003428,795.256],
          [-8.168318,42.002767,783.723],
          [-8.167792,42.002634,774.134],
          [-8.167264,42.002196,770.055],
          [-8.166719,42.002474,771.31],
          [-8.166289,42.002441,774.207],
          [-8.165877,42.002706,769.962],
          [-8.165697,42.002957,767.527],
          [-8.165731,42.00372,763.368],
          [-8.165595,42.003704,762.973],
          [-8.165599,42.003344,764.824],
          [-8.165382,42.003006,765.412],
          [-8.164089,42.003102,765.389],
          [-8.164269,42.003699,757.017],
          [-8.163832,42.004249,760.787],
          [-8.163774,42.004669,763.615],
          [-8.162658,42.004634,778.807],
          [-8.162557,42.005028,780.69],
          [-8.16207,42.005592,784.735],
          [-8.161403,42.005656,789.486],
          [-8.160778,42.006053,800.081],
          [-8.160456,42.006738,801.262],
          [-8.160299,42.007648,806.53],
          [-8.159802,42.007994,814.157],
          [-8.159848,42.00914,813.813],
          [-8.159385,42.009395,823.743],
          [-8.159232,42.009807,827.207],
          [-8.158297,42.010459,836.207],
          [-8.15773,42.010543,844.42],
          [-8.157569,42.010851,841.565],
          [-8.156704,42.010847,856.626],
          [-8.15634,42.011073,855.191],
          [-8.155906,42.011765,848.942],
          [-8.155771,42.012465,846.57],
          [-8.155203,42.01324,856.227],
          [-8.154618,42.013314,862.744],
          [-8.153787,42.013697,861.805],
          [-8.152643,42.013475,851.315],
          [-8.151771,42.012819,843.507],
          [-8.150547,42.01237,835.543],
          [-8.149928,42.011899,831.448],
          [-8.149401,42.011851,829.659],
          [-8.148886,42.011984,829.244],
          [-8.148686,42.011614,831.455],
          [-8.148196,42.01253,837.923],
          [-8.147646,42.013079,843.91],
          [-8.146826,42.013476,857.214],
          [-8.146625,42.013795,859.654],
          [-8.145822,42.014347,876.009],
          [-8.145327,42.014888,886.807],
          [-8.145288,42.015111,886.48],
          [-8.144998,42.015097,898.815],
          [-8.144536,42.01597,913.476],
          [-8.144098,42.016323,931.041],
          [-8.143781,42.016842,948.122],
          [-8.143896,42.017535,958.49],
          [-8.143491,42.0176,968.067],
          [-8.143496,42.017753,971.136],
          [-8.142749,42.017807,987.717],
          [-8.142629,42.01814,999.018],
          [-8.142341,42.018257,1008.307],
          [-8.14222,42.018657,1020.272],
          [-8.141882,42.018725,1027.047],
          [-8.141603,42.019058,1037.158],
          [-8.140699,42.019536,1044.433],
          [-8.140111,42.019533,1042.384],
          [-8.140076,42.019868,1041.532],
          [-8.13917,42.020892,1026.759],
          [-8.138983,42.02143,1022.118],
          [-8.13904,42.021664,1022.298],
          [-8.138328,42.021066,1013.817],
          [-8.138284,42.020595,1013.007],
          [-8.137988,42.020171,1014.19],
          [-8.137989,42.01977,1018.625],
          [-8.137169,42.018961,1030.426],
          [-8.137029,42.018333,1037.131],
          [-8.137045,42.016428,1041.322],
          [-8.13686,42.016008,1041.24],
          [-8.135925,42.015979,1031.377],
          [-8.135051,42.015686,1033.665],
          [-8.134578,42.015234,1037.168],
          [-8.134196,42.014345,1046.214],
          [-8.133344,42.013823,1053.007],
          [-8.133217,42.012987,1064.695],
          [-8.132613,42.012421,1067.24],
          [-8.132131,42.012784,1063.641],
          [-8.13148,42.013591,1061.776],
          [-8.130838,42.01393,1069.527],
          [-8.130016,42.013757,1086.675],
          [-8.129215,42.013898,1099.332],
          [-8.128439,42.014474,1106.772],
          [-8.128001,42.015005,1109.013],
          [-8.127428,42.015197,1109.414],
          [-8.126804,42.015991,1102.107],
          [-8.126769,42.016222,1101.046],
          [-8.126268,42.016617,1090.551],
          [-8.125825,42.016196,1093.152],
          [-8.125276,42.016127,1097.687],
          [-8.12528,42.016444,1093.318],
          [-8.124235,42.017535,1099.386],
          [-8.124089,42.01817,1099.833],
          [-8.123508,42.018659,1111.052],
          [-8.123126,42.020129,1113.053],
          [-8.123367,42.020439,1110.908],
          [-8.12316,42.020592,1110.907],
          [-8.123947,42.02121,1102.754],
          [-8.127124,42.022075,1089.219],
          [-8.128153,42.022871,1090.53],
          [-8.12825,42.023308,1096.195],
          [-8.128642,42.02371,1096.156],
          [-8.129908,42.023783,1086.054],
          [-8.130222,42.023529,1075.451],
          [-8.130756,42.02354,1066.525],
          [-8.130954,42.02338,1057.526],
          [-8.133044,42.024324,1037.108],
          [-8.133832,42.024309,1025.307],
          [-8.13477,42.024659,1013.971],
          [-8.135913,42.024547,996.696],
          [-8.137394,42.025523,988.527],
          [-8.138405,42.026795,989.822],
          [-8.138529,42.026737,986.913],
          [-8.138526,42.0259,981.781],
          [-8.138868,42.025543,981.638],
          [-8.139355,42.025385,984.792],
          [-8.139989,42.025554,990.173],
          [-8.141101,42.025361,1001.22],
          [-8.141476,42.024437,995.307],
          [-8.141717,42.024191,993.878],
          [-8.141764,42.02347,995.363],
          [-8.142257,42.022804,991.254],
          [-8.143177,42.022384,977.899],
          [-8.144122,42.022837,965.15],
          [-8.145395,42.022743,961.268],
          [-8.146118,42.022939,957.985],
          [-8.146763,42.022395,951.038],
          [-8.146756,42.023326,960.87],
          [-8.146609,42.023503,962.517],
          [-8.146539,42.0243,966.507],
          [-8.145805,42.025155,964.232],
          [-8.145415,42.025369,959.067],
          [-8.145276,42.025656,957.538],
          [-8.145356,42.025869,957.963],
          [-8.145189,42.026081,955.774],
          [-8.145517,42.026318,958.232],
          [-8.145492,42.026738,951.169],
          [-8.145848,42.027046,948.316],
          [-8.145846,42.027426,944.476],
          [-8.146507,42.027886,938.413],
          [-8.147304,42.02799,935.007],
          [-8.147687,42.028299,937.92],
          [-8.148818,42.030727,978.115],
          [-8.149755,42.031882,975.826],
          [-8.151075,42.032902,979.761],
          [-8.151484,42.033009,977.542],
          [-8.152723,42.032952,959.37],
          [-8.1537,42.03323,955.997],
          [-8.154752,42.033189,948.739],
          [-8.155352,42.033494,951.801],
          [-8.155858,42.033419,944.621],
          [-8.15619,42.033088,939.707],
          [-8.156021,42.032644,939.382],
          [-8.156453,42.031908,935.877],
          [-8.157481,42.031312,936.428],
          [-8.157756,42.030855,939.163],
          [-8.157799,42.030442,940.934],
          [-8.158288,42.030511,942.422],
          [-8.158426,42.030046,943.569]
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