import { Component, OnInit, Input } from '@angular/core';
import * as Highcharts from 'highcharts';
import HC_exporting from 'highcharts/modules/exporting';

@Component({
  selector: 'app-widget-area',
  templateUrl: './area.component.html',
  styleUrls: ['./area.component.css']
})
export class AreaComponent implements OnInit {

  @Input() data = [];

  chartOptions: {};
  Highcharts = Highcharts;

  constructor() { }

  ngOnInit(): void {

    //Hard-coded exemple:
    this.chartOptions = {
      chart: {
        type: 'area'
      },
      title: {
        text: 'Area chart exemple'
      },
      subtitle: {
        text: 'Demo'
      },
      tooltip: {
        split: true,
        valueSuffix: ' millions'
      },
      credits: {
        enable: false
      },
      exporting: {
        enable: true,
      },
      series: this.data
    };

    // For admnin downlolad the chart
    HC_exporting(Highcharts);

    // For resize the chart if needed
    setTimeout(() => {
      window.dispatchEvent(
        new Event('resize')
      );
    }, 300);
  }

}
