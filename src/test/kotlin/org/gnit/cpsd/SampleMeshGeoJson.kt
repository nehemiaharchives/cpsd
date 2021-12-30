package org.gnit.cpsd

val sampleMeshPointsWithReach = """
    {
      "type": "FeatureCollection",
      "name": "100m-mesh-points",
      "crs": {
        "type": "name",
        "properties": {
          "name": "urn:ogc:def:crs:OGC:1.3:CRS84"
        }
      },
      "features": [
        {
          "type": "Feature",
          "properties": {
            "reach": null
          },
          "geometry": {
            "type": "Point",
            "coordinates": [
              141.390625, 42.69958333333334
            ]
          }
        },
        {
          "type": "Feature",
          "properties": {
            "reach": 1500
          },
          "geometry": {
            "type": "Point",
            "coordinates": [
                141.40437500000002, 42.74208333333333
            ]
          }
        }
      ]
    }
""".trimIndent()

val sampleMeshPolygonWithReach = """
    {
      "type": "FeatureCollection",
      "name": "100m-mesh",
      "crs": {
        "type": "name",
        "properties": {
          "name": "urn:ogc:def:crs:OGC:1.3:CRS84"
        }
      },
      "features": [
        {
          "type": "Feature",
          "properties": {
            "P2010TT": 2.0,
            "P2025TT": 1.915344,
            "P2040TT": 1.724868,
            "reach": null
          },
          "geometry": {
            "type": "MultiPolygon",
            "coordinates": [
              [
                [
                  [
                    141.39,
                    42.699166666666663
                  ],
                  [
                    141.39,
                    42.7
                  ],
                  [
                    141.39125,
                    42.7
                  ],
                  [
                    141.39125,
                    42.699166666666663
                  ],
                  [
                    141.39,
                    42.699166666666663
                  ]
                ]
              ]
            ]
          }
        },
        {
          "type": "Feature",
          "properties": {
            "P2010TT": 0.516531,
            "P2025TT": 0.494667,
            "P2040TT": 0.445474,
            "reach": 1500
          },
          "geometry": {
            "type": "MultiPolygon",
            "coordinates": [
              [
                [
                  [
                    141.40375,
                    42.741666666666667
                  ],
                  [
                    141.40375,
                    42.7425
                  ],
                  [
                    141.405,
                    42.7425
                  ],
                  [
                    141.405,
                    42.741666666666667
                  ],
                  [
                    141.40375,
                    42.741666666666667
                  ]
                ]
              ]
            ]
          }
        }
      ]
    }
""".trimIndent()

val sampleMeshPolygon = """
    {
      "type": "FeatureCollection",
      "name": "100m-mesh",
      "crs": {
        "type": "name",
        "properties": {
          "name": "urn:ogc:def:crs:OGC:1.3:CRS84"
        }
      },
      "features": [
        {
          "type": "Feature",
          "properties": {
            "P2010TT": 2.0,
            "P2025TT": 1.915344,
            "P2040TT": 1.724868
          },
          "geometry": {
            "type": "MultiPolygon",
            "coordinates": [
              [
                [
                  [
                    141.39,
                    42.699166666666663
                  ],
                  [
                    141.39,
                    42.7
                  ],
                  [
                    141.39125,
                    42.7
                  ],
                  [
                    141.39125,
                    42.699166666666663
                  ],
                  [
                    141.39,
                    42.699166666666663
                  ]
                ]
              ]
            ]
          }
        },
        {
          "type": "Feature",
          "properties": {
            "P2010TT": 0.516531,
            "P2025TT": 0.494667,
            "P2040TT": 0.445474
          },
          "geometry": {
            "type": "MultiPolygon",
            "coordinates": [
              [
                [
                  [
                    141.40375,
                    42.741666666666667
                  ],
                  [
                    141.40375,
                    42.7425
                  ],
                  [
                    141.405,
                    42.7425
                  ],
                  [
                    141.405,
                    42.741666666666667
                  ],
                  [
                    141.40375,
                    42.741666666666667
                  ]
                ]
              ]
            ]
          }
        }
      ]
    }
""".trimIndent()