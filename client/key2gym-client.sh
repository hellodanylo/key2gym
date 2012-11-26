#!/bin/sh

#
# Use this script to run Key2Gym client.
#

java -cp "lib/*:key2gym-client.jar" -splash:splash.png org.key2gym.client.Main $@