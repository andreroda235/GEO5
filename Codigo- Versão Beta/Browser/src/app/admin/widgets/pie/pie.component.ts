import { Component, OnInit, Input } from '@angular/core';
import * as Highcharts from 'highcharts';
import HC_exporting from 'highcharts/modules/exporting';

@Component({
  selector: 'app-widget-pie',
  templateUrl: './pie.component.html',
  styleUrls: ['./pie.component.scss']
})
export class PieComponent implements OnInit {

  @Input() data = [];

  Highcharts = Highcharts;
  chartOptions = {};

  constructor() { }

  ngOnInit(): void {
    //Hard-coded exemple:
    this.chartOptions = {
      chart: {
        plotBackgroundColor: null,
        ploltBorderWidth: null,
        plotShadow: false,
        type:'pie'
      },
      title: {
        text: 'Pie Chart Example'
      },
      tooltip: {
       pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
      },
      plotOptions: {
        pie: {
          allowPointSelect: true,
          cursor: 'pointer',
          dataLabels: {
            enable: true,
            format: '<b>{point.name}</b>: {point.percentage:.1f}%</b>'
          }
        }
      },
      exporting: {
        enable: true
      },
      credits: {
        enable: false
      },
      series: [
        { name: 'Brands',
          colorByPoint: true,
          data: this.data
        }        
      ]
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
