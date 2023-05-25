import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MapCamInfoComponent } from './map-cam-info.component';

describe('MapCamInfoComponent', () => {
  let component: MapCamInfoComponent;
  let fixture: ComponentFixture<MapCamInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MapCamInfoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MapCamInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
