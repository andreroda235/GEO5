import { Component } from '@angular/core';


@Component({
    selector: 'load-comp',
    template: `
    <style>
    #circle {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%,-50%);
	width: 150px;
    height: 150px;	
}

.loader {
    width: calc(100% - 0px);
	height: calc(100% - 0px);
	border: 8px solid #48D1CC;
	border-top: 8px solid #008080;
	border-radius: 50%;
	animation: rotate 5s linear infinite;
}

@keyframes rotate {
100% {transform: rotate(360deg);}
} 
    </style>



 
    <div id="circle">
      <div class="loader">
        <div class="loader">
            <div class="loader">
               <div class="loader">
    
               </div>
            </div>
        </div>
      </div>
    </div> 
    `
  })
  export class LoadDummyComponent  {
    
    
      
  }