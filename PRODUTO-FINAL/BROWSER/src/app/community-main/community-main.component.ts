import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-community-main',
  templateUrl: './community-main.component.html',
  styleUrls: ['./community-main.component.css']
})
export class CommunityMainComponent implements OnInit {

  constructor() { }

  feeds:any =[
    {
      id:"sfge",
      image:'../../../assets/logogeo5.png',
      date:'30/04/20 17:25',
      likes:'5666',
      text:"I'm baby kale chips affogato ennui lumbersexual, williamsburg paleo quinoa iceland normcore tumeric. Kitsch coloring book retro, seitan schlitz tattooed biodiesel vexillologist neutra. Synth mumblecore deep v, umami selfies normcore gluten-free snackwave. Seitan ramps drinking vinegar venmo keytar, humblebrag VHS post-ironic tacos godard pour-over."
      //....
    },
    {
      image:'../../../assets/logogeo5.png',
      date:'30/04/20 17:25',
      likes:'555',
      text:"I'm baby kale chips affogato ennui lumbersexual, williamsburg paleo quinoa iceland normcore tumeric. Kitsch coloring book retro, seitan schlitz tattooed biodiesel vexillologist neutra. Synth mumblecore deep v, umami selfies normcore gluten-free snackwave. Seitan ramps drinking vinegar venmo keytar, humblebrag VHS post-ironic tacos godard pour-over."
    },
    {
      image:'../../../assets/logogeo5.png',
      date:'30/04/20 17:25',
      likes:'555',
      text:"I'm baby kale chips affogato ennui lumbersexual, williamsburg paleo quinoa iceland normcore tumeric. Kitsch coloring book retro, seitan schlitz tattooed biodiesel vexillologist neutra. Synth mumblecore deep v, umami selfies normcore gluten-free snackwave. Seitan ramps drinking vinegar venmo keytar, humblebrag VHS post-ironic tacos godard pour-over."
    },
    {
      image:'../../../assets/logogeo5.png',
      date:'30/04/20 17:25',
      likes:'555',
      text:"I'm baby kale chips affogato ennui lumbersexual, williamsburg paleo quinoa iceland normcore tumeric. Kitsch coloring book retro, seitan schlitz tattooed biodiesel vexillologist neutra. Synth mumblecore deep v, umami selfies normcore gluten-free snackwave. Seitan ramps drinking vinegar venmo keytar, humblebrag VHS post-ironic tacos godard pour-over."
    }
  ]


  ngOnInit(): void {
  }

}
