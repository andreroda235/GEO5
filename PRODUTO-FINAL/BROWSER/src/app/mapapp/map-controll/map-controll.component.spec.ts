import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MapControllComponent } from './map-controll.component';

describe('MapControllComponent', () => {
  let component: MapControllComponent;
  let fixture: ComponentFixture<MapControllComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MapControllComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MapControllComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
